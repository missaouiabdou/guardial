require 'base64'
require 'net/http'

module Api
  module V1
    class VulnerabilitiesController < ApplicationController
      # Disable CSRF / authorization checks for this demo API.
      # Rails API mode doesn't include CSRF protection by default.

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

      # 6. Reflected Cross-Site Scripting (XSS)
      # Try payload: name=<script>alert('XSS')</script>
      def xss
        name = params[:name] || "Guest"
        # Vulnerable Reflected XSS: rendering unescaped user parameter directly as HTML using html_safe
        render html: "<h1>Hello, #{name}</h1>".html_safe
      end

      # 7. Server-Side Request Forgery (SSRF)
      # Try payload: url=http://127.0.0.1:3000/api/v1/vulnerabilities/xss
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
    end
  end
end
