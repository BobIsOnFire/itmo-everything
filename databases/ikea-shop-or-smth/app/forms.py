from flask_wtf import FlaskForm
from wtforms import StringField, PasswordField, BooleanField, SubmitField
from wtforms.fields.core import IntegerField
from wtforms.fields.simple import HiddenField
from wtforms.validators import DataRequired, Email, EqualTo, ValidationError

from app.models import User

class LoginForm(FlaskForm):
    email = StringField('Email', validators = [DataRequired(), Email()])
    password = PasswordField('Password', validators = [DataRequired()])
    remember_me = BooleanField('Remember Me')
    submit = SubmitField('Sign In')

class RegistrationForm(FlaskForm):
    username = StringField('Username', validators=[DataRequired()])
    email = StringField('Email', validators=[DataRequired(), Email()])
    password = PasswordField('Password', validators=[DataRequired()])
    password2 = PasswordField('Repeat Password', validators=[DataRequired(), EqualTo('password')])
    submit = SubmitField('Register')

    def validate_username(self, username):
        user = User.query.filter_by(name=username.data).first()
        if user is not None:
            raise ValidationError('Please use a different username.')

    def validate_email(self, email):
        user = User.query.filter_by(email=email.data).first()
        if user is not None:
            raise ValidationError('Please use a different email address.')

class AddItemForm(FlaskForm):
    item = HiddenField()
    count = IntegerField('Count', validators=[DataRequired()])
    submit = SubmitField('Add to Cart')

class AddOrderForm(FlaskForm):
    delivery_requested = BooleanField('Is delivery required?')
    address = StringField('Address')
    delivery_time = StringField('Deliver at')
    assembly_requested = BooleanField('Do you need furniture assembly on your delivery?')
    submit = SubmitField('Create order')
