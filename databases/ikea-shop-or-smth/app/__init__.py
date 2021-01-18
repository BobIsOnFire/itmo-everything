from flask import Flask
from config import Config
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate
from flask_login import LoginManager
from flask_bootstrap import Bootstrap

app = Flask('coursach', template_folder='app/templates', static_folder='app/static')
app.config.from_object(Config)

bootstrap = Bootstrap(app)

db = SQLAlchemy(app)
migrate = Migrate(app, db)

login = LoginManager(app)
login.login_view = 'login'

class ShoppingCart():
    def __init__(self):
        self.cart = list()
    
    def find(self, item_id):
        for i in range(len(self.cart)):
            if self.cart[i][0] == item_id:
                return i
        return -1

    def add(self, item_id, count):
        if count < 0:
            self.remove(item_id, -count)
            return
        if count == 0:
            return
        
        i = self.find(item_id)
        if i >= 0:
            self.cart[i][1] += count
        else:
            self.cart.append((item_id, count))

    def remove(self, item_id, count):
        if count < 0:
            self.add(item_id, -count)
            return
        if count == 0:
            return

        i = self.find(item_id)
        if i < 0:
            return
        
        if self.cart[i][1] <= count:
            self.cart.pop(i)
        else:
            self.cart[i][1] -= count

    def remove_item(self, item_id):
        i = self.find(item_id)
        if i >= 0:
            self.cart.pop(i)
    
    def clear(self):
        self.cart = list()

    def get(self):
        return self.cart

shopping_cart = ShoppingCart()

from app import routes, models, errors
