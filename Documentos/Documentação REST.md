#1. Endpoints de Utilizadores
## Listar todos os utilizadores
**GET** /api/users
**Exemplo**

[{"account_id": 1, "account_name": "Filomeno Sabino", "account_email": "filomenosabino@gmail.com}]

## Criar utilizadores
**POST** /api/users/create/users
**Exemplo**
[{"account_name": "Filomeno Sabino", "account_username": "therealsabino", "account_email": "filomenosabino@gmail.com", "password": "Teste11", "account_dob": "29-06-2004", "account_gender": "Male"}]
