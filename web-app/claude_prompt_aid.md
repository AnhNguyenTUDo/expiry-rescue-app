# Table schema

// Use DBML to define your database structure

// Docs: [https://dbml.dbdiagram.io/docs](https://dbml.dbdiagram.io/docs)

// DB names: expiry_rescue_db
// PostgreSQL

Table users {

  id integer [primary key]

  username varchar [unique, not null]

  email varchar [unique]

  phone integer

  password_hash varchar [not null]

  full_name varchar

  is_active boolean [default: true]

  created_at timestamp

  updated_at timestamp

  deleted_at timestamp

}

Table staffs {

  id integer [primary key]

  username varchar [unique, not null]

  email varchar [unique]

  phone integer

  password_hash varchar [not null]

  role varchar [not null, note: 'admin, staff']

  full_name varchar

  is_active boolean [default: true]

  created_at timestamp

  updated_at timestamp

  deleted_at timestamp

}

Table supermarkets {

  id integer [primary key]

  name varchar [not null]

  adress varchar [not null]

  district varchar [not null]
  
  phone integer

  contact_person varchar

  operating_hours_from timestamp

  operating_hours_to timestamp

  is_active boolean [default: true]

  created_at timestamp

  updated_at timestamp

  deleted_at timestamp

}

Table orders {

  id integer [primary key]

  order_number varchar [unique, not null, note: 'human-readable order ID']

  user_id integer [ref: > users.id, not null] // many-to-one

  status varchar [default: 'pending', note: 'pending, confirmed, cancelled']

  total_amount decimal [not null]

  created_at timestamp

  updated_at timestamp

  deleted_at timestamp

}

Table order_items {

  id integer [primary key]

  order_id integer [ref: > orders.id, not null] //many-to-one

  product_inventory_id integer [ref: > product_inventory.id, not null] //many-to-many

  product_name varchar

  supermarket_name varchar

  quantity integer [not null]

  unit varchar

  price decimal [not null, note: 'price at time of order']

  subtotal decimal [not null]

  created_at timestamp

  original_price decimal [not null, note: 'original price at time of order']

}

Table feedbacks {

  id integer [primary key]

  user_id integer [ref: > users.id, not null] // many-to-one

  order_id integer [ref: - orders.id, note: 'optional: link to specific order']

  // product_id integer [ref: - products.id, note: 'optional: link to specific product']

  rating integer [not null, note: '1-5 stars']

  content text

  is_reviewed boolean [default: false, note: 'admin has reviewed']

  is_approved boolean [default: false, note: 'approve or reject']

  admin_response text

  created_at timestamp  

  updated_at timestamp

  deleted_at timestamp

}

// Master product catalog - defines WHAT the product is

Table product_master {

  id integer [primary key]

  name varchar [not null, note: 'Coca Cola 330ml']

  category_id integer [ref: > product_categories.id, not null]

  description text

  unit varchar [note: 'bottle, can, pack, kg, etc']

  standard_image_url varchar [note: 'generic product image']

  days_before_expiry integer [note: 'default safety buffer']

  is_active boolean [default: true]

  created_at timestamp [default: `now()`]

  updated_at timestamp

  deleted_at timestamp

}

// Actual inventory - WHERE the product is available

Table product_inventory {

  id integer [primary key]

  product_master_id integer [ref: > product_master.id, not null]

  supermarket_id integer [ref: > supermarkets.id, not null]

  // Pricing

  original_price decimal(10,2) [not null, note: 'supermarket original price']

  selling_price decimal(10,2) [not null, note: 'your discounted price']

  // Inventory details

  quantity_available integer [not null, default: 0]

  expiry_date timestamp [not null]

  // Status

  status varchar [default: 'available', note: 'available, not_available']

  // Audit

  created_by integer [ref: > users.id, note: 'staff who added this inventory']

  created_at timestamp [default: `now()`]

  updated_at timestamp

  deleted_at timestamp

}

Table product_categories {

  id integer

  name varchar [not null]

  default_days_before_expiry integer [not null]

  created_at timestamp

  updated_at timestamp

  deleted_at timestamp

}