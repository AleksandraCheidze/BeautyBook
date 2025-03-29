import requests
import json

BASE_URL = 'http://localhost:8081/api'

def test_register_admin():
    url = f'{BASE_URL}/users/register'
    data = {
        'email': 'admin@test.com',
        'password': 'admin123',
        'role': 'ADMIN'
    }
    response = requests.post(url, json=data)
    print('Register Admin:', response.status_code, response.text)
    return response.json() if response.ok else None

def test_login():
    url = f'{BASE_URL}/auth/login'
    data = {
        'email': 'admin@test.com',
        'password': 'admin123'
    }
    response = requests.post(url, json=data)
    print('Login:', response.status_code, response.text)
    return response.json()['accessToken'] if response.ok else None

def test_create_categories(token):
    url = f'{BASE_URL}/categories'
    headers = {'Authorization': f'Bearer {token}'}
    categories = [
        {'name': 'Manicure', 'description': 'All types of manicure'},
        {'name': 'Pedicure', 'description': 'All types of pedicure'},
        {'name': 'Haircut', 'description': 'All types of haircuts'}
    ]
    
    for category in categories:
        response = requests.post(url, json=category, headers=headers)
        print(f'Create Category {category["name"]}:', response.status_code, response.text)

def test_get_categories():
    url = f'{BASE_URL}/categories'
    response = requests.get(url)
    print('Get Categories:', response.status_code, response.text)
    return response.json() if response.ok else []

def test_create_procedures(token, category_id):
    url = f'{BASE_URL}/procedures'
    headers = {'Authorization': f'Bearer {token}'}
    procedures = [
        {
            'name': 'Classic Manicure',
            'description': 'Basic manicure service',
            'duration': 60,
            'price': 30.0,
            'categoryId': category_id
        },
        {
            'name': 'Gel Polish',
            'description': 'Long-lasting gel polish',
            'duration': 90,
            'price': 45.0,
            'categoryId': category_id
        }
    ]
    
    for procedure in procedures:
        response = requests.post(url, json=procedure, headers=headers)
        print(f'Create Procedure {procedure["name"]}:', response.status_code, response.text)

def test_get_procedures():
    url = f'{BASE_URL}/procedures'
    response = requests.get(url)
    print('Get Procedures:', response.status_code, response.text)

def main():
    # Register admin
    admin = test_register_admin()
    
    # Login and get token
    token = test_login()
    if not token:
        print('Failed to get token')
        return
    
    # Test categories
    test_create_categories(token)
    categories = test_get_categories()
    
    # Test procedures
    if categories:
        test_create_procedures(token, categories[0]['id'])
    test_get_procedures()

if __name__ == '__main__':
    main() 