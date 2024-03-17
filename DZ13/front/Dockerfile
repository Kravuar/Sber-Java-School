FROM node:alpine AS development

ENV NODE_ENV development

WORKDIR /react-app

COPY ./package*.json /react-app
RUN npm install
COPY . .

EXPOSE 3000
CMD ["npm","start"]