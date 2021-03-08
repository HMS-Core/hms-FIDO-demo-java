## FIDO BioAuthn Sample
[![License](https://img.shields.io/badge/Docs-hmsguides-brightgreen)](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/introduction-0000001051069988)

中文 | [English](README.md)

## 目录

 * [简介](#简介)
 * [开始](#开始)
 * [安装](#安装)
 * [配置](#配置)
 * [支持环境](#支持环境)
 * [样例代码](#样例代码)
 * [许可证](#许可证)


## 简介
FIDO BioAuthn Sample 提供了许多示例代码供参考。

## 开始
更多开发详细信息，请参阅以下链接：

开发指南: https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/bioauthn-sdk-0000001055606575

API参考: https://developer.huawei.com/consumer/cn/doc/development/HMSCore-References-V5/bioauthnoverview-0000001050268268-V5

我们还提供了一个示例来演示Android上BioAuthn SDK的使用。

这个示例使用gradle编译。

首先通过克隆此代码库或下载快照来下载演示代码。

在 Android Studio中，使用"Open an existing Android Studio project"，然后选择"fido-bioauthn-sample"的目录。

您需要在AppGallery Connect中创建一个应用，并获取agconnect-services.json文件并添加到项目中。您还需要生成签名证书指纹并将证书文件添加到项目中，并将配置添加到 build.gradle。请参阅[AppGallery Connect配置](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/config-agc-0000001050262772)指南，在 AppGallery Connect中配置应用。

在build.gradle中，使用你自己的applicationId替换掉 "com.huawei.hms.fido.sample.bioauthn"。

你可以使用"gradlew build"命令来构建你的项目。


## 安装
在使用FIDO BioAuthn Sample代码前，请检查你的java开发环境Android Studio是否安装。
解压FIDO BioAuthn Sample代码zip包。

## 支持环境
推荐使用Java 1.7及以上版本。

## 配置
无需更多配置。

## 样例代码

1）. 展示没有关联加密对象的fingerprint manager，并允许用户使用设备 PIN 和密码进行认证。

2）. 展示关联了加密对象的fingerprint manager。

3）. 向用户设备发送面部认证请求。


## 结果
<center class="half">
<img src="images/result.png" width=250 />
</center>

## 许可证
此示例代码已获得[Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0)。
