FROM node:alpine AS development

ENV NODE_ENV development

WORKDIR /react-app

COPY ./package*.json /react-app
RUN npm install
COPY . .

#ARG REACT_APP_BACKEND_URL
#ENV REACT_APP_BACKEND_URL $REACT_APP_BACKEND_URL

CMD ["npm","start"]