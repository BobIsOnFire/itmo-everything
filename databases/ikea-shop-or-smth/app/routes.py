from flask import render_template, flash, redirect, url_for, request
from flask.helpers import send_from_directory
from flask_login import current_user, login_user, logout_user, login_required
from werkzeug.urls import url_parse

from app import app, db
from app.forms import LoginForm, RegistrationForm
from app.models import Item, StoreRoom, User, get_item_content, get_item_data, get_order_content, get_order_data, get_order_history, get_user_data

@app.route('/')
@app.route('/index')
def index():
    all_rooms = db.session.query(StoreRoom).all()
    return render_template('index.html', rooms = all_rooms[1:9] + all_rooms[12:23])

@app.route('/room/<room_id>')
def room(room_id):
    current_room = db.session.query(StoreRoom).get(room_id)
    items = db.session.query(Item).filter(Item.store_room_id == room_id).all()
    return render_template('room.html', current_room = current_room, items = items)

@app.route('/item/<item_id>')
def item(item_id):
    item_data = get_item_data(item_id)
    content = get_item_content(item_id)
    colors = ['#{0:06x}'.format(part.color) for part in content]
    part_data = [(content[i], colors[i]) for i in range(len(content))]
    return render_template('item.html', item_data = item_data, part_data = part_data)

@app.route('/static/<path:path>')
def get_static_resource(path):
    return send_from_directory('static', path)

@app.route('/login', methods = ['GET', 'POST'])
def login():
    if current_user.is_authenticated:
        return redirect(url_for('index'))
    
    form = LoginForm()
    if form.validate_on_submit():
        user = User.query.filter_by(email=form.email.data).first()
        if user is None or not user.check_password(form.password.data):
            flash('Invalid email or password')
            return redirect(url_for('login'))
        
        login_user(user, remember=form.remember_me.data)
        next_page = request.args.get('next')
        if not next_page or url_parse(next_page).netloc != '':
            return redirect(url_for('index'))
        return redirect(next_page)
    
    return render_template('login.html', title='Sign In', form=form)

@app.route('/register', methods = ['GET', 'POST'])
def register():
    if current_user.is_authenticated:
        return redirect(url_for('index'))
    
    form = RegistrationForm()
    if form.validate_on_submit():
        user = User(username=form.username.data, email=form.email.data)
        user.set_password(form.password.data)
        db.session.add(user)
        db.session.commit()
        flash('Registered. Now you can login!')
        return redirect(url_for('login'))
    
    return render_template('register.html', title='Register', form=form)

@app.route('/logout')
def logout():
    logout_user()
    return redirect(url_for('login'))

@app.route('/profile')
@login_required
def user():
    user_data = get_user_data(current_user.id)
    orders = get_order_history(current_user.id)
    return render_template('user.html', user_data = user_data, orders = orders)

@app.route('/order/<order_id>')
@login_required
def order(order_id):
    order_data = get_order_data(order_id)
    if order_data.customer_id != current_user.id:
        flash('You do not have access to view this order.')
        return redirect(url_for('invalid_access'))
    
    content = get_order_content(order_id)
    total_price = 0
    for item in content:
        total_price += item.price * item.item_count

    return render_template('order.html', order_data = order_data, content = content, total_price = total_price)

@app.route('/invalid')
def invalid_access():
    return render_template('invalid_access.html')
