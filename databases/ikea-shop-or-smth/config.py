import os

class Config():
    CSRF_ENABLED = True
    SECRET_KEY = os.environ.get('SECRET_KEY') or 'fuck you bob'
