class User < ApplicationRecord
  has_secure_password
  has_many :private_notes, dependent: :destroy
end
