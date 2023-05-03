
# 第一阶段：构建镜像
FROM node:14.21.3 AS builder

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build

# 第二阶段：运行镜像
FROM node:14.21.3-slim

WORKDIR /app

COPY --from=builder /app/dist /app/dist

EXPOSE 8080

CMD ["node", "/app/dist/server.js"]

# # 第一阶段：构建镜像
# FROM node:14.21.3 AS builder

# WORKDIR /app

# COPY package*.json ./

# RUN npm install

# COPY . .

# RUN npm run build

# # 第二阶段：运行镜像
# FROM scratch

# WORKDIR /app

# COPY --from=builder /app/dist /app/dist

# EXPOSE 8080

# CMD ["/app/dist/server.js"]

# 还是太大了
# # 第一阶段：构建镜像
# FROM node:14.21.3 AS builder

# WORKDIR /app

# COPY package*.json ./

# RUN npm install

# COPY . .

# # 第二阶段：运行镜像
# FROM node:14.21.3-slim

# WORKDIR /app

# COPY --from=builder /app/node_modules /app/node_modules
# COPY --from=builder /app .

# EXPOSE 8080

# CMD ["npm", "run", "serve"]

# FROM node:14.21.3

# WORKDIR /app

# COPY package*.json ./

# RUN npm install

# COPY . .

# EXPOSE 8080

# CMD ["npm", "run", "serve"]

# # 使用 Node.js 14 镜像构建 Vue 应用程序
# FROM node:14 AS builder
# WORKDIR /app
# COPY package*.json ./
# RUN npm install
# COPY . .
# RUN npm run build

# # 使用 Nginx 镜像部署 Vue 应用程序
# FROM nginx:1.21
# COPY --from=builder /app/dist /usr/share/nginx/html
# COPY nginx.conf /etc/nginx/conf.d/default.conf

# # 暴露 8080 端口
# EXPOSE 8080
