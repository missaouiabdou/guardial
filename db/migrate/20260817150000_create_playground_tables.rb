class CreatePlaygroundTables < ActiveRecord::Migration[8.1]
  def change
    create_table :products do |t|
      t.string :name
      t.decimal :price, precision: 8, scale: 2
      t.text :description
      t.timestamps
    end

    create_table :users do |t|
      t.string :username
      t.string :password_digest
      t.boolean :admin, default: false
      t.timestamps
    end

    create_table :comments do |t|
      t.string :author
      t.text :content
      t.timestamps
    end
  end
end
