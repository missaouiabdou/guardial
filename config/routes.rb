Rails.application.routes.draw do
  # Reveal health status on /up that returns 200 if the app boots with no exceptions, otherwise 500.
  get "up" => "rails/health#show", as: :rails_health_check

  namespace :api do
    namespace :v1 do
      # Exactly 7 Vulnerabilities endpoints
      get "vulnerabilities/sqli", to: "vulnerabilities#sqli"
      post "vulnerabilities/rce", to: "vulnerabilities#rce"
      get "vulnerabilities/lfi", to: "vulnerabilities#lfi"
      post "vulnerabilities/deserialize", to: "vulnerabilities#deserialize"
      post "vulnerabilities/users", to: "vulnerabilities#create_user"
      get "vulnerabilities/comments", to: "vulnerabilities#comments"
      post "vulnerabilities/comments", to: "vulnerabilities#create_comment"
      get "vulnerabilities/ssrf", to: "vulnerabilities#ssrf"
    end
  end
end
