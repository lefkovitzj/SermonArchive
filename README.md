<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->
<a id="readme-top"></a>
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
[![project_license][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]



<!-- PROJECT LOGO -->
<br />
<div align="center">
<h3 align="center">Sermon Archive</h3>

  <p align="center">
    A Spring Boot Microservice that serves as a Media Management API for church sermon recordings
    <br />
    <a href="https://github.com/lefkovitzj/SermonArchive"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/lefkovitzj/SermonArchive/issues/new?labels=bug&template=bug-report---.md">Report Bug</a>
    &middot;
    <a href="https://github.com/lefkovitzj/SermonArchive/issues/new?labels=enhancement&template=feature-request---.md">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

This project serves as a tool for storing, classifying, and accessing media archives of church sermon series.
It is a backend-only API solution currently, though might be augmented with a frontend at a later point or by 
another contributor. 

This project is intended to serve as my practice and learning playground prior to starting an internship for 
Summer 2026 in which I will need to use Spring Boot. As a result, many mistakes may be made and code may not be done 
optimally the first time around. I also am relying extensively on online examples, documentation, and Artificial 
Intelligence tools (GitHub Copilot, Google Gemini in particular) for general Spring Boot advice and when 
troubleshooting errors I haven't seen before. 

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Built With

* [![DockerShield][Docker]][Docker-url]
* [![SpringBootShield][Spring Boot]][Spring-boot-url]
* [![JavaShield][Java]][java-url]
* [![MavenShield][Maven]][Maven-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- ROADMAP -->
## Roadmap

- [x] Account creation for churches
- [x] Media upload
  - [x] S3 file storage integration 
  - [x] Audio recording upload 
  - [x] Video recording upload
- [x] Media access
  - [x] Media streaming
  - [x] Media download
- [x] Media classification
  - [x] Sermon series
  - [x] Preacher/speaker
  - [x] Date
- [x] Add logging
- [ ] Containerize for AWS ECR
  - [x] Create multistage Dockerfile
  - [ ] Create GitHub action to rebuild and publish to ECR
  - [ ] Create GitHub action to rebuild and publish to GHCR
  - [ ] Create GitHub action to rebuild and publish to DockerHub
- [ ] Add test suite
  - [ ] Define test cases
  - [ ] Create GitHub actions for test CI

See the [open issues](https://github.com/lefkovitzj/SermonArchive/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE.txt` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTACT -->
## Contact

Joseph Lefkovitz - [@lefkovitzj](https://github.com/lefkovitzj) - contact@lefkovitzj.com

Project Link: [https://github.com/lefkovitzj/SermonArchive](https://github.com/lefkovitzj/SermonArchive)

<p align="right">(<a href="#readme-top">back to top</a>)</p>




<!-- MARKDOWN LINKS & IMAGES -->
[contributors-shield]: https://img.shields.io/github/contributors/lefkovitzj/SermonArchive.svg?style=for-the-badge
[contributors-url]: https://github.com/lefkovitzj/SermonArchive/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/lefkovitzj/SermonArchive.svg?style=for-the-badge
[forks-url]: https://github.com/lefkovitzj/SermonArchive/network/members
[stars-shield]: https://img.shields.io/github/stars/lefkovitzj/SermonArchive.svg?style=for-the-badge
[stars-url]: https://github.com/lefkovitzj/SermonArchive/stargazers
[issues-shield]: https://img.shields.io/github/issues/lefkovitzj/SermonArchive.svg?style=for-the-badge
[issues-url]: https://github.com/lefkovitzj/SermonArchive/issues
[license-shield]: https://img.shields.io/github/license/lefkovitzj/SermonArchive.svg?style=for-the-badge
[license-url]: https://github.com/lefkovitzj/SermonArchive/blob/main/LICENSE
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/joseph-lefkovitz

[Java]: https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white
[java-url]: https://www.oracle.com/java/technologies/downloads/#java25
[Spring Boot]: https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white
[Spring-boot-url]: https://spring.io/projects/spring-boot
[Docker]: https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white
[Docker-url]: https://www.docker.com/
[Maven]: https://img.shields.io/badge/apache_maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white
[Maven-url]: https://maven.apache.org/
[PostgreSQL]: https://img.shields.io/badge/postgresql-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white
[Postgres-url]: https://www.postgresql.org/
[AWS]: https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white
[AWS-url]: https://aws.amazon.com/ecs/
