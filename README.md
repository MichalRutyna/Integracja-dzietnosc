## Running
First create a folder in the same directory as docker-compose.yml, named secrets. 
Inside, place db_name.txt, db_password.txt, db_user.txt, jwt_secret.txt

Then, for dev, do `docker compose up -d --build`.
For production use `docker compose up -d --build -f docker-compose.yml`.