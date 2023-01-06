<div id="top"></div>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->

<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![Apache-2.0][license-shield]][license-url]

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/github_username/repo_name">
    <img src="https://assets.brandfolder.com/q2tsde-8kenzk-4cg1pz/v/8222261/original/Yubico%20Logo%20Big%20(PNG).png" alt="Logo" width="363" height="100">
  </a>

<h3 align="center">Passkey Relying Party Example</h3>

  <p align="center">
    Sample backend application to demonstrate a relying party that supports passkeys. This example includes examples of the java-webauthn-server library, helper classes, code examples, best practices, and API schemas.
    <br />
    <a href="https://github.com/YubicoLabs/passkey-relying-party-example/tree/master#about-the-project"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    ·
    <a href="https://github.com/YubicoLabs/passkey-relying-party-example">Report Bug</a>
    ·
    <a href="https://github.com/YubicoLabs/passkey-relying-party-example">Request Feature</a>
  </p>
</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The project</a>
    </li>
    <li>
      <a href="#built-with">Built with</a>
    </li>
    <li>
      <a href="#getting-started">Getting started</a>
    </li>
    <li><a href="#next-steps">Next steps</a></li>
    <li><a href="#faqs-and-common-issues">FAQs and common issues</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->

## About The Project

This project provides a sample relying party that supports passkeys. The objective is to provide samples that demonstrate to developers how to build a custom passkey enabled applications.

Our example is architected in a way for a developer to take advantage of multiple interfaces so that they may use the database, identity provider, and cloud environment in which they operate in. While the architectural components may differ from app to app, the fundamental logic remains fairly consistent between passkey applications.

Some of the features included in this project are:

- Examples of the primary registration and authentication methods used by passkey applications
- Defined REST API schema that can be leveraged across web and native applications
- Interfaces for plugging in your own database and identity provider
- Leveraging attestation and the FIDO Metadata Service

<!-- For insight on how to build your own relying party from scratch, please visit Yubico's passkey workshop for step-by-step instructions on how to build the sample shown in this project + more.

@TODO - Uncomment this once we have a repo for our guide
-->

**Disclaimer** - This project is not meant to act as a production ready solution for **all** organizations. Please review the code, and make any modifications, so that it matches up to your security stack and policies.

<p align="right">(<a href="#top">back to top</a>)</p>

## Built With

- [Java](<https://en.wikipedia.org/wiki/Java_(programming_language)>)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Yubico's java-webauthn-server library](https://github.com/Yubico/java-webauthn-server)
- [FIDO Metadata Service](https://fidoalliance.org/metadata/)
- [AWS SAM](https://aws.amazon.com/serverless/sam/)

<p align="right">(<a href="#top">back to top</a>)</p>

## Getting Started

The project, as-is, supports two forms of deployments:

- locally
- To AWS

### Local deployment

Run the command below to deploy the application locally. The application/API will be accessible from localhost:8080

```bash
./mvnw spring-boot:run
```

### AWS deployment

Our sample uses AWS SAM to deploy the application to AWS. For this deployment you will need to ensure that you have downloaded the AWS CLI, and that the CLI has the correct credentials to deploy the various resources used in the project.

Once you are ready un the commands below to deploy the application locally.

```bash
sam build

sam deploy
```

### Note about data storage

Unless configured otherwise, this application will store data in-memory. Meaning, if the application is terminated, then all credentials will be lost. This application is meant to act as a demonstration, and not a production ready solution.

You may follow these steps to configure [REST OF THE CONTENT TO BE ADDED] <!-- Add additional blurb here for the different examples we are going to highlight, and to the workshop for creating your own data store -->

<p align="right">(<a href="#top">back to top</a>)</p>

## Next steps

@TODO - To add content later

<p align="right">(<a href="#top">back to top</a>)</p>

## FAQs and Common Issues

@TODO - To add content later

<p align="right">(<a href="#top">back to top</a>)</p>

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- LICENSE -->

## License

Distributed under the Apache-2.0 License. See `LICENSE` for more information.

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- CONTACT -->

## Contact

[Yubico Developer Program](https://developers.yubico.com/)

Project Link: [https://github.com/YubicoLabs/yed-spoke-example](https://github.com/YubicoLabs/passkey-relying-party-example)

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[contributors-shield]: https://img.shields.io/github/contributors/YubicoLabs/passkey-relying-party-example.svg?style=for-the-badge
[contributors-url]: https://github.com/YubicoLabs/passkey-relying-party-example/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/YubicoLabs/passkey-relying-party-example.svg?style=for-the-badge
[forks-url]: https://github.com/YubicoLabs/passkey-relying-party-example/network/members
[stars-shield]: https://img.shields.io/github/stars/YubicoLabs/passkey-relying-party-example.svg?style=for-the-badge
[stars-url]: https://github.com/YubicoLabs/passkey-relying-party-example/stargazers
[issues-shield]: https://img.shields.io/github/issues/YubicoLabs/passkey-relying-party-example.svg?style=for-the-badge
[issues-url]: https://github.com/YubicoLabs/passkey-relying-party-example/issues
[license-shield]: https://img.shields.io/github/license/YubicoLabs/passkey-relying-party-example.svg?style=for-the-badge
[license-url]: https://github.com/YubicoLabs/passkey-relying-party-example/blob/master/LICENSE
