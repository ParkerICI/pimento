lein do clean, uberjar
heroku deploy:jar target/uberjar/pimento-standalone.jar --jdk=24.0.1 --app pimento

