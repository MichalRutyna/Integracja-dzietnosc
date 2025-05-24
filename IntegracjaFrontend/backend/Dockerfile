FROM node:18-alpine

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY package*.json ./

RUN npm install

COPY . .

ENV TZ=Europe/Warsaw

USER appuser

EXPOSE 3001

CMD npm start 