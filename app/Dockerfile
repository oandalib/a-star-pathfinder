FROM node:14-buster

RUN apt-get update
RUN apt install -y default-jre

WORKDIR /app
COPY . .
RUN npm install

CMD ["npm", "start"]
