require 'base64'
require 'net/http'
require 'erb'

module Api
  module V1
    class VulnerabilitiesController < ApplicationController
      # Disable CSRF / authorization check for this demo API
      # Rails API m      # Rails API mode doesn't include CSRF protection by default, but we ensure it remains accessible.
      # Rails API mode doesn't include CSRF protection by default, but we ensure it remains accessible.

      # 1. SQL Injection (SQLi)
      # Try payload: query=Shield' OR '1'='1
      def sqli
        query = params[:query]
        # Vulnerable string interpolation in raw SQL select query
        sql = "SELECT * FROM products WHERE name LIKE '%#{query}%'"
        @products = Product.connection.select_all(sql)
        render json: @products
		
		
      end

      # 2. Remote Command Execution (RCE)
      # Try payload: ip=127.0.0.1 & dir (Windows) or ip=127.0.0.1 ; ls (Linux)
      def rce
        ip = params[:ip]
        if ip.present?
          # Vulnerable shell command execution using backticks with raw string interpolation
          output = `ping -n 1 #{ip}`
          render json: { command: "ping -n 1 #{ip}", output: output }
        else
          render json: { error: "IP parameter is missing" }, status: :bad_request
        end
      end

      # 3. Path Traversal / Local File Inclusion (LFI)
      # Try payload: file=../../config/database.yml
      def lfi
        file = params[:file]
        if file.present?
          # Vulnerable: direct file path assembly without input sanitization or directory sandboxing
          file_path = Rails.root.join("public", file)
          if File.exist?(file_path)
            render plain: File.read(file_path)
          else
            render json: { error: "File not found at #{file_path}" }, status: :not_found
          end
        else
          render json: { error: "File parameter is missing" }, status: :bad_request
        end
      end

      # 4. Insecure Deserialization
      # Try payload: Base64 serialized objects
      def deserialize
        payload = params[:payload]
        if payload.present?
          decoded = Base64.decode64(payload)
          # Vulnerable: unsafe deserialization using Marshal.load
          obj = Marshal.load(decoded)
          render json: { status: "success", class: obj.class.name, inspect: obj.inspect }
        else
          render json: { error: "Payload parameter is missing" }, status: :bad_request
        end
      rescue => e
        render json: { error: e.message }, status: :unprocessable_entity
      end

      # 5. Mass Assignment / Privilege Escalation
      # Try payload: {"user": {"username": "attacker", "password": "password123", "admin": true}}
      def create_user
        # Vulnerable mass assignment: permitting all parameters (.permit!)
        user_params = params.require(:user).permit!
        @user = User.new(user_params)
        if @user.save
          render json: { status: "success", user: @user }
        else
          render json: { status: "error", errors: @user.errors.full_messages }, status: :unprocessable_entity
        end
      end

      # 6. Hardcoded Secrets / Sensitive Data Exposure
      def secrets
        # Vulnerable: plain-text keys hardcoded in the codebase
        aws_key_id = "AKIAIOSFODNN7INVALIDKEY"
        aws_secret_key = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
        github_token = "ghp_1234567890abcdefghijklmnopqrstuvwxyz"

        render json: {
          aws_key_id: aws_key_id,
          aws_secret_key: aws_secret_key,
          github_token: github_token,
          warning: "Hardcoded credentials detected!"
        }
      end

      # 7. Stored Cross-Site Scripting (XSS)
      def comments
        @comments = Comment.all
        render json: @comments
      end

      def create_comment
        # Vulnerable Stored XSS: content is saved as-is without validation/escaping
        @comment = Comment.new(author: params[:author], content: params[:content])
        if @comment.save
          render json: { status: "success", comment: @comment }
        else
          render json: { status: "error", errors: @comment.errors.full_messages }, status: :unprocessable_entity
        end
      end

      # 8. Server-Side Request Forgery (SSRF)
      # Try payload: url=http://127.0.0.1:3000/api/v1/vulnerabilities/secrets
      def ssrf
        url_str = params[:url]
        if url_str.present?
          uri = URI(url_str)
          # Vulnerable SSRF: fetches arbitrary user-supplied URL without host/IP whitelisting
          response = Net::HTTP.get_response(uri)
          render json: {
            status_code: response.code,
            body: response.body[0..5000] # Limit size for response beauty
          }
        else
          render json: { error: "URL parameter is missing" }, status: :bad_request
        end
      rescue => e
        render json: { error: e.message }, status: :unprocessable_entity
      end

      # 9. Server-Side Template Injection (SSTI)
      # Try payload: name=<%= Dir.entries('.') %> or name=<%= 7*7 %>
      def ssti
        name = params[:name] || "Guest"
        if name.present?
          # Vulnerable SSTI: interpolating user-supplied input into the template string before ERB compilation
          template_str = "Hello #{name}, welcome! Present time is <%= Time.now %>."
          renderer = ERB.new(template_str)
          output = renderer.result(binding)
          render plain: output
        else
          render json: { error: "Name parameter is missing" }, status: :bad_request
        end
      rescue => e
        render json: { error: e.message }, status: :unprocessable_entity
      end

      # 10. IDOR (Insecure Direct Object Reference)
      # Try payload: id=1 (admin note) vs id=2 (alice note)
      def note
        note_id = params[:id]
        if note_id.present?
          # Vulnerable IDOR: directly querying by ID without validating ownership or session context
          @note = PrivateNote.find(note_id)
          render json: @note
        else
          render json: { error: "ID parameter is missing" }, status: :bad_request
        end
      rescue ActiveRecord::RecordNotFound
        render json: { error: "Note not found" }, status: :not_found
      end
    end
  end
end
