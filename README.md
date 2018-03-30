# Uaithne Java
Agile backend architecture oriented to improve the productivity of the development team using Java.

## Goals
-   Easy application for backend development
-   Regardless of its complexity or size
-   Allow to grow without increasing complexity or reduce maintainability
-   Allow to modify the behaviour easily
-   Allow to improve the productivity of the team involved in the application development

## Philosophy
Get the backend resemble a **set of LEGO**, where there are **highly interchangeable pieces** that are **made up to achieve** the desired result.

## Architecture
The architecture design is explained in it own repository. This repository contains the code generator according to the design expressed in the Uaithne architecture.

**Uaithne architecture project**: [Uaithne Java](https://github.com/juanluispaz/uaithne-java)

## Generator
Improving the productivity of the development team has been one of the great motivations behind the design of the architecture of Uaithne, and it alone represents a great improvement over the classical n-tier architecture. After implementing the new architecture there was still room to improve productivity, automatically generating code that is responsible for handling the verbosity required by Java, and above all, that generates the access code to the database with its respective SQL queries that handles most operations.

Uaithne is accompanied, optionally, with a code generator that allows to generate (and regenerate) the operations and entities from a small definition, as well as the logic of access to database required by them, using MyBatis as database access  framework. In this way, only those activities that really require human work are left to the programmer due to the decisions that must be made by him.

The Uaithne architecture and the code generator working together have proved in a multitude of projects of different sizes to be a very useful tool for the programming of the backend of applications, and during this time, achieving a high productivity of the development team.

## Manual
- **English**: [Uaithne Java (EN).pdf](https://github.com/juanluispaz/uaithne-generator-java/raw/develop/Uaithne%20Generator%20Java%20(EN).pdf)
- **Spanish**: [Uaithne Java (ES).pdf](https://github.com/juanluispaz/uaithne-generator-java/raw/develop/Uaithne%20Generator%20Java%20(ES).pdf)

## License

LGPL-3.0
