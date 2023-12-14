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
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->

## About The Project

Passkeys are the long awaited replacement for passwords. While passwords have been the primary standard in securing user accounts, they are not without issues. Passwords are easy to forget, and easily phishable; which has led to security breaches in various industries such as energy, healthcare, and technology.

What does this mean for your application, and your users? As adoption continues to increase, your users will expect that your application allows them to leverage their passkey supported devices to securely and seamlessly authenticate into their accounts.

This project provides a sample application that demonstrates a full end-to-end passkey solution. The objective is to demonstrate a working prototype to help remove some uncertainty that your development team may encounter on your road to adopting passkeys in your application.

Our example is architected in a way for a developer to take advantage of multiple interfaces so that they may use the database, identity provider, and cloud environment in which they operate in. While the architectural components may differ from app to app, the fundamental logic remains fairly consistent between passkey applications.

Some of the features included in this project are a:

- Working web and mobile client applications to test different passkey user flows
- Working backend application with APIs that can process, store, and validate passkeys sent by any of your clients
- Demonstration on how to enable passkey with an OpenID-Connect identity provider
- Set of best practices for storing passkeys in a database

**Disclaimer** - This project is not meant to act as a production ready solution. Please review and understand the code, then integrate the needed components, and make any modifications based on your security requirements.

<p align="right">(<a href="#top">back to top</a>)</p>

## Built With

- [Java](<https://en.wikipedia.org/wiki/Java_(programming_language)>)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Yubico's java-webauthn-server library](https://github.com/Yubico/java-webauthn-server)
- [React](https://react.dev/)
- [Swift](https://developer.apple.com/swift/)
- [MySQL](https://www.mysql.com/)
- [Keycloak](https://www.keycloak.org/)
- [Docker](https://www.docker.com/)
- [FIDO Metadata Service](https://fidoalliance.org/metadata/)

<p align="right">(<a href="#top">back to top</a>)</p>

## Getting Started

To begin your journey, click the link below for our full walkthrough on our passkey application.

[Link to Yubico's passkey workshop](https://yubicolabs.github.io/passkey-workshop/)

Follow the steps below for a quick deployment.

1. Clone the repository

```bash
git clone https://github.com/YubicoLabs/passkey-workshop.git
```

2. Navigate to the deploy folder

```bash
cd passkey-workshop/deploy
```

3. Run the deployment script

```bash
# For Mac and Linux
./deploy.sh

# For Windows (Powershell)
\deploy.ps1
```

4. Open the client app at [localhost:3000](http://localhost:3000)

## Next steps

Still curious about passkey development? The resources below may help to strengthen your understanding

- [passkeys.dev](https://passkeys.dev)
- [Passkey guidance for web and mobile apps - Yubico webinar](https://www.brighttalk.com/webcast/15793/553636)

Do you have a working solution? Share it with the community! The links below are resources where you can share your deployment, and have it reviewed to tell the world that your solution supports passkeys.

- [Works with Yubikey](https://www.yubico.com/works-with-yubikey/)
- [Known passkey support](https://github.com/passkeydeveloper/discussions/wiki/Known-Passkey-Support)

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

[Report an issue](https://github.com/YubicoLabs/passkey-workshop/issues)

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
