from flask import Flask
from config import Config

app = Flask('coursach', template_folder='app/templates', static_folder='app/static')
app.config.from_object(Config)

from app import routes
