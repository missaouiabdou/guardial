# Clear existing data
Product.destroy_all
User.destroy_all
Comment.destroy_all

# Seed Products
Product.create!([
  { name: "Super Secure Shield", price: 99.99, description: "Protects against all physical attacks" },
  { name: "GuardiaX Firewall", price: 149.99, description: "A high-performance virtual firewall" },
  { name: "Anas Quantum Decryptor", price: 999.99, description: "Experimental quantum decryption engine" }
])

# Seed Users
admin = User.create!(username: "admin", password: "AdminSuperSecretPassword2026", admin: true)
alice = User.create!(username: "alice", password: "alicepassword123", admin: false)
bob = User.create!(username: "bob", password: "bobpassword456", admin: false)

# Seed Comments (Stored XSS targets)
Comment.create!(author: "Hacker", content: "<b onmouseover=\"alert('XSS Reflected/Stored')\" style=\"color: red; cursor: pointer;\">Hover over me!</b>")
Comment.create!(author: "Alice", content: "Great utility dashboard, thanks for making this!")

puts "Successfully seeded database with 7-vuln playground data."
