## 如何运行前端项目
1. 安装依赖``npm install``
2. 运行serve ``npm run serve``
> 如何在install时出错，大概率是前端npm里面的各种版本问题

> 注意版本nodejs的版本，前端会有很多问题，不推荐使用最新的版本，我使用的是``nodejs-v12.13.0``与``npm-v6.12.0``,能跑

如果在安装时出现node-sass版本问题的话，可以试试这个指令：(就是换个源，这玩意就是一个坑没办法)  `npm config set sass_binary_site=https://npm.taobao.org/mirrors/node-sass`
**再遇到问题就上网找吧，你绝对不是那个最倒霉的那个，肯定有人遇到和你一样的错误。**

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
<!--[![LinkedIn][linkedin-shield]][linkedin-url]-->


<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/Jarrettluo/document-sharing-site">
    <img src="https://github.com/Jarrettluo/document-sharing-site/blob/main/images/banner.png" alt="Logo" width="300" height="300">
  </a>

<h3 align="center">全文档</h3>

  <p align="center">
    支持全文检索的文档分享、存储系统。
    <br />
  </p>
</p>





<!-- ABOUT THE PROJECT -->

## 关于全文档

在小团队中往往会产生大量的协作文档。例如，我们会将各类文档放在网盘、svn等软件中，但是存在文档内的内容无法快速搜索的问题。因此，专门开发了一个用于存储ppt、word、png等文档的，支持私有部属的知识库的检索。


<p>体验地址：<a href="http://81.69.247.172/#/">http://81.69.247.172/#/</a></p>

### 开源地址

```
前端项目 https://github.com/Jarrettluo/all-documents-vue.git
后端项目 https://github.com/Jarrettluo/document-sharing-site.git
```

选择mongoDB作为主要的数据库，存储文档和文件。

后端技术：SpringBoot + MongoDB + ES

前端技术：Vue + axios

- docx预览方案：docx

- ppt预览方案：暂无

- excel预览方案：xx

<!-- ROADMAP -->

## 路线图

查看 [open issues](https://github.com/othneildrew/Best-README-Template/issues) 。



<!-- CONTRIBUTING -->

## 提交代码

一起来贡献

1. `Fork` 该项目
2. 创建自己的分支 (`git checkout -b feature/AmazingFeature`)
3. 提交你的功能 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个PR

<!-- LICENSE -->

## 许可证

查看 `LICENSE` 文件。



<!-- CONTACT -->

## 联系我

Jarrett Luo - luojiarui2@163.com


<!-- ACKNOWLEDGEMENTS -->

## 致谢

- 暂无

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[contributors-shield]: https://img.shields.io/github/contributors/Jarrettluo/document-sharing-site.svg?style=for-the-badge

[contributors-url]: https://github.com/Jarrettluo/document-sharing-site/graphs/contributors

[forks-shield]: https://img.shields.io/github/forks/Jarrettluo/document-sharing-site.svg?style=for-the-badge

[forks-url]: https://github.com/Jarrettluo/document-sharing-site/network/members

[stars-shield]: https://img.shields.io/github/stars/Jarrettluo/document-sharing-site.svg?style=for-the-badge

[stars-url]: https://github.com/Jarrettluo/document-sharing-site/stargazers

[issues-shield]: https://img.shields.io/github/issues/Jarrettluo/document-sharing-site.svg?style=for-the-badge

[issues-url]: https://github.com/Jarrettluo/document-sharing-site/issues

[license-shield]: https://img.shields.io/github/license/Jarrettluo/document-sharing-site.svg?style=for-the-badge

[license-url]: https://github.com/Jarrettluo/document-sharing-site/blob/master/LICENSE.txt

[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555

[linkedin-url]: https://linkedin.com/in/othneildrew

[product-screenshot]: images/screenshot.png
