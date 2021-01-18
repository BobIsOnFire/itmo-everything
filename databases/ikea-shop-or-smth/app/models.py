from app import db, login
from datetime import datetime
from werkzeug.security import generate_password_hash, check_password_hash
from flask_login import UserMixin

dept_enum = db.Enum(
    'sales',
    'engineering',
    'delivery',
    'support',
    'higher management',
    name='department', create_type=False
)

class Employee(db.Model):
    __tablename__ = 'employee'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(64), index=True, nullable=False)
    email = db.Column(db.String(120), index=True, nullable=False, unique=True)
    department = db.Column(dept_enum, nullable=False)
    wage = db.Column(db.Numeric, nullable=False)

class User(UserMixin, db.Model):
    __tablename__ = 'user_account'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(64), index=True, nullable=False)
    email = db.Column(db.String(120), index=True, nullable=False, unique=True)
    family_card_id = db.Column(db.Integer, db.ForeignKey('family_card.id'))
    password_hash = db.Column(db.String(128), nullable=False)

    def set_password(self, password):
        self.password_hash = generate_password_hash(password)

    def check_password(self, password):
        return check_password_hash(self.password_hash, password)

    def __repr__(self):
        return '<User {}>'.format(self.username)

class FamilyCard(db.Model):
    __tablename__ = 'family_card'

    id = db.Column(db.Integer, primary_key=True)
    level = db.Column(db.Integer)
    points = db.Column(db.Integer)
    release_time = db.Column(db.DateTime)

class StoreRoom(db.Model):
    __tablename__ = 'store_room'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(64), index=True, nullable=False, unique=True)
    length = db.Column(db.Numeric, nullable=False)
    width = db.Column(db.Numeric, nullable=False)
    responsible_id = db.Column(db.Integer, db.ForeignKey('employee.id'), nullable=False)

class StoreNavigation(db.Model):
    __tablename__ = 'store_navigation'
    from_id = db.Column(db.Integer, db.ForeignKey('store_room.id'), nullable=False, primary_key=True)
    to_id = db.Column(db.Integer, db.ForeignKey('store_room.id'), nullable=False, primary_key=True)

class Item(db.Model):
    __tablename__ = 'item'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(64), index=True, nullable=False, unique=True)
    price = db.Column(db.Numeric, nullable=False)
    length = db.Column(db.Numeric, nullable=False)
    width = db.Column(db.Numeric, nullable=False)
    height = db.Column(db.Numeric, nullable=False)
    in_stock_storage = db.Column(db.Boolean, nullable=False)
    in_stock_shop = db.Column(db.Boolean, nullable=False)
    store_room_id = db.Column(db.Integer, db.ForeignKey('store_room.id'))

class ItemPart(db.Model):
    __tablename__ = 'item_part'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(64), index=True, nullable=False)
    length = db.Column(db.Numeric, nullable=False)
    width = db.Column(db.Numeric, nullable=False)
    height = db.Column(db.Numeric, nullable=False)
    color = db.Column(db.Integer, nullable=False)
    material = db.Column(db.String(64), nullable=False)
    part_count = db.Column(db.Integer, nullable=False)
    item_id = db.Column(db.Integer, db.ForeignKey('store_room.id'), nullable=False)

class Order(db.Model):
    __tablename__ = 'customer_order'

    id = db.Column(db.Integer, primary_key=True)
    order_time = db.Column(db.DateTime, nullable=False)
    resolve_time = db.Column(db.DateTime)
    customer_id = db.Column(db.Integer, db.ForeignKey('user_account.id'), nullable=False)
    responsible_id = db.Column(db.Integer, db.ForeignKey('employee.id'))

class OrderContent(db.Model):
    __tablename__ = 'order_content'

    order_id = db.Column(db.Integer, db.ForeignKey('customer_order.id'), nullable=False, primary_key=True)
    item_id = db.Column(db.Integer, db.ForeignKey('item.id'), nullable=False, primary_key=True)
    item_count = db.Column(db.Integer, nullable=False)

class DeliveryOrder(db.Model):
    __tablename__ = 'delivery_order'

    id = db.Column(db.Integer, primary_key=True)
    address = db.Column(db.String(64), nullable=False)
    delivery_time = db.Column(db.DateTime, nullable=False)
    assembly_ordered = db.Column(db.Boolean, nullable=False)
    responsible_id = db.Column(db.Integer, db.ForeignKey('employee.id'))
    order_id = db.Column(db.Integer, db.ForeignKey('customer_order.id'), nullable=False, unique=True)

class UserAccountData(db.Model):
    name = db.Column(db.String(64), primary_key=True)
    price = db.Column(db.Numeric)
    length = db.Column(db.Numeric)
    width = db.Column(db.Numeric)
    height = db.Column(db.Numeric)
    in_stock_storage = db.Column(db.Boolean)
    in_stock_shop = db.Column(db.Boolean)
    store_room = db.Column(db.String(64))
    item_count = db.Column(db.Integer)

class ItemContent(db.Model):
    name = db.Column(db.String(64), primary_key=True)
    length = db.Column(db.Numeric)
    width = db.Column(db.Numeric)
    height = db.Column(db.Numeric)
    color = db.Column(db.Integer)
    material = db.Column(db.String(64))
    part_count = db.Column(db.Integer)

class PgFunctionWrapper():
    def get_user_data(self, user_id):
        user_data_cols = [
            db.column('user_id'),
            db.column('name'),
            db.column('email'),
            db.column('card_level'),
            db.column('card_points'),
            db.column('card_release_time')
        ]

        stmt = db.select(user_data_cols).select_from(db.func.get_user_data(user_id))
        return db.session.execute(stmt).first()

    def get_order_history(self, user_id):
        order_data_cols = [
            db.column('order_id'),
            db.column('order_time'),
            db.column('delivery_requested'),
            db.column('address'),
            db.column('delivery_time'),
            db.column('assembly_ordered'),
            db.column('resolved'),
            db.column('resolve_time')
        ]

        stmt = db.select(order_data_cols).select_from(db.func.get_order_history(user_id))
        return db.session.execute(stmt).fetchall()

    def get_order_data(self, order_id):
        order_data_cols = [
            db.column('customer_id'),
            db.column('order_id'),
            db.column('order_time'),
            db.column('delivery_requested'),
            db.column('address'),
            db.column('delivery_time'),
            db.column('assembly_ordered'),
            db.column('resolved'),
            db.column('resolve_time')
        ]

        stmt = db.select(order_data_cols).select_from(db.func.get_order_data(order_id))
        return db.session.execute(stmt).first()


    def get_order_content(self, order_id):
        order_content_cols = [
            db.column('item_id'),
            db.column('name'),
            db.column('price'),
            db.column('length'),
            db.column('width'),
            db.column('height'),
            db.column('in_stock_storage'),
            db.column('in_stock_shop'),
            db.column('store_room'),
            db.column('item_count')
        ]

        stmt = db.select(order_content_cols).select_from(db.func.get_order_content(order_id))
        return db.session.execute(stmt).fetchall()

    def get_item_data(self, item_id):
        item_data_cols = [
            db.column('item_id'),
            db.column('name'),
            db.column('price'),
            db.column('length'),
            db.column('width'),
            db.column('height'),
            db.column('in_stock_storage'),
            db.column('in_stock_shop'),
            db.column('store_room')
        ]

        stmt = db.select(item_data_cols).select_from(db.func.get_item_data(item_id))
        return db.session.execute(stmt).first()

    def get_item_content(self, item_id):
        item_content_cols = [
            db.column('name'),
            db.column('length'),
            db.column('width'),
            db.column('height'),
            db.column('color'),
            db.column('material'),
            db.column('part_count')
        ]

        stmt = db.select(item_content_cols).select_from(db.func.get_item_content(item_id))
        return db.session.execute(stmt).fetchall()

    def add_order(self, user_id, cart):
        stmt = db.select([db.column('add_order')]).select_from(db.func.add_order(user_id, *cart.get()))
        return db.session.execute(stmt).fetchone().add_order

    def add_delivery_order(self, address, assembly_requested, delivery_time, main_order_id):
        stmt = db.select([db.column('add_delivery_order')]).select_from(
            db.func.add_delivery_order(address, assembly_requested, delivery_time, main_order_id))
        return db.session.execute(stmt).fetchone().add_delivery_order

pg_functions = PgFunctionWrapper()

@login.user_loader
def load_user(id):
    return User.query.get(int(id))
