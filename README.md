![altime banner](https://github.com/user-attachments/assets/3044f087-39e6-417c-9cfe-e89b9c7b251e)

<br id="top">
<p align="center">
    <a href="#about">About</a>  |
    <a href="#backlogs--user-stories">Backlogs & User Stories</a>  |
    <a href="#prototype--documentation">Prototype & Documentation</a>  |
    <a href="#technologies">Technologies</a>  |
    <a href="#team">Team</a>
</p>

<span id="about">

## :bookmark_tabs: About the project

Based on the challenge presented by the partner company, the solution is a time-tracking system with three areas of use. The system allows registering companies, employees and their specific information, enabling centralized and efficient management.

It includes an interactive dashboard that gives a detailed view of all activities, making it easier to analyze and track data related to people and process management. Through this dashboard, administrators can make decisions based on metrics, patterns and indicators relevant to performance and internal organization.

> _Project based on the SCRUM agile methodology, aiming to develop the Proactivity, Autonomy, Collaboration and Results Delivery of the students involved_

:pushpin: **Project Status:** Finished ✅

### 🏁 Sprint Deliveries

Each delivery was made by creating a **tag** for each module (Backend, Frontend and Docs), plus a branch in this repository with a full report of everything developed in that sprint. See the list below:

| Sprint | Due date   | Status        | History                                                                    |
|--------|------------|---------------|----------------------------------------------------------------------------|
| 01     | 03/30/2025 | ✔️ Done       | [view report](https://github.com/User-Standart/altime-time-tracking/tree/Sprint-1)     |
| 02     | 04/27/2025 | ✔️ Done       | [view report](https://github.com/User-Standart/altime-time-tracking/tree/Sprint-2)     |
| 03     | 05/25/2025 | ✔️ Done       | [view report](https://github.com/User-Standart/altime-time-tracking/tree/Sprint-3)     |

### :clapper: Final Presentation

Below is a demo of the features for each type of system user:

<details>
   <summary>Administrator</summary>
   <div align="center">
      <img src="docs/Docs/tela%20login%202.gif" alt="Login Screen Demo" />
   </div>
</details>

→ [Back to top](#top)

<span id="backlogs--user-stories">

## :clipboard: System Requirements

<details>
<summary>Functional and Non-Functional Requirements</summary>

<br>

| Req. No. | Description                                 | Type                |
|----------|---------------------------------------------|---------------------|
| FR1      | Build a registration interface for companies and professionals, including a photo | Functional |
| FR2      | Filtering by date, company and professional | Functional          |
| FR3      | Allow report export                         | Functional          |
| FR4      | Dashboard with charts and filtering         | Functional          |
| FR5      | API to consume the data (nice to have)      | Functional          |
| NFR1     | Minimalist frontend                         | Non-Functional      |
| NFR2     | Installation guide                          | Non-Functional      |
| NFR3     | API documentation                           | Non-Functional      |
| NFR4     | Database modeling                           | Non-Functional      |

</details>

## :dart: Backlogs & User Stories

<details>
<summary>Backlog with User Stories and Estimates</summary>

<br>

| Rank | Functional Requirement | User Story | Estimate | Sprint | Acceptance Criteria |
|------|------------------------|------------|----------|--------|---------------------|
| 1  | FR1                 | As a system user, I want to register companies and employees in the system so that the project can be managed | 10h | 1 | The system must allow registering companies and employees with required fields, ensuring the data is persisted in the database. |
| 2  | NFR4                | As a system Administrator, I want a database where all system information will be stored | 12h | 1 | There must be a structured, secure and optimized database to store all essential system information. |
| 3  | FR3                 | As a system user, I want to manually export reports in PDF and CSV format to work with the data in other ways | 8h | 1 | The system must allow exporting reports in PDF and CSV, with filters selectable before generating the file. |
| 4  | NFR2, NFR3          | As a system administrator, I want an installation and usage guide so the system can be used by many different users | 6h | 1 | There must be an installation manual and a detailed usage guide with step-by-step instructions. |
| 5  | NFR1                | As a system user, I want a login screen when entering the system so I can sign up or log into my account according to my privileges | 8h | 2 | The system must allow sign-up and login with validation. |
| 6  | NFR1                | As a system user, I want the system to store employees' clock-ins and clock-outs so they can be computed | 6h | 2 | The system must save the recorded times. |
| 7  | NFR1                | As a system user, I want to be able to correct time entries in case an entry is wrong or needs to be changed | 7h | 3 | It must be possible to edit time entries with a justification. |
| 8  | FR2                 | As a system user, I want to view data through charts and a dashboard to have an interactive way to see the data | 12h | 2 | The system must present data through interactive charts. |
| 9  | NFR1                | As a system user, I want to be able to delete an employee's registered information | 6h | 3 | The system must allow deleting employee data. |
| 10 | NFR1                | As a system user, I want to be able to delete a company's registered information | 6h | 3 | The system must allow deleting company data. |
| 11 | FR4                 | As a system user, I want to be able to change an employee's registered information to fix incorrect data | 6h | 2 | The system must allow editing employee data. |
| 12 | FR5                 | As a system user, I want to be able to change a registered company's data to keep the information up to date | 5h | 2 | The system must allow editing company data. |
| 13 | FR1, NFR1           | As a system user, I want the CPF field to use an input mask to make it easier to fill in | 2h | 3 | The CPF field must only accept valid, formatted input. |
| 14 | FR1, NFR1           | As a system user, I want the CNPJ field to use an input mask to make it easier to fill in | 2h | 3 | The CNPJ field must only accept valid, formatted input. |
| 15 | NFR1                | As a system user, I want lists to be paginated to make it easier to navigate through many records | 4h | 2 | The system must split data into pages with navigation. |
| 16 | NFR3, NFR1          | As a system user, I want the photo to be larger in the generated PDF for better viewing | 3h | 3 | The system must adjust the image size in the PDF report. |
| 17 | NFR1                | As a developer, I want to use Lombok in the backend to reduce repetitive code | 2h | 2 | The backend project must use Lombok to generate boilerplate code. |
| 18 | NFR1                | As a developer, I want to implement unit tests in the backend to ensure the integrity of the features | 6h | 3 | The system must contain automated tests covering the main functions. |
| 19 | NFR4                | As a developer, I want to use Supabase for authentication and cloud data persistence | 10h | 3 | The system must be integrated with Supabase and use its features. |
| 20 | FR5                 | As a system user, I want to export the report through an API so it can be used in other projects | 10h | 3 | Create endpoints, format the response and validate the integration. |

</details>

</details>

→ [Back to top](#top)

<span id="prototype--documentation">

## :desktop_computer: Prototype & Documentation

As part of the project planning, wireframes were created to design the layout. Once validated by the client, they were applied to a prototype built in Figma ([click here to open Figma](https://www.figma.com/board/fyhWp4Ji3oQa5PNxootLjf/User-Standart---Sistema-de-Registro-de-Pontos?node-id=0-1&p=f&t=zvkWaiQgHAmyolei-0)), allowing users to interact with the interface.

> 🔗 **General links**
> - **Software documentation:** [click here](docs/Docs/Guia%20de%20Instalac%CC%A7a%CC%83o.pdf)
> - **User manual:** [click here](docs/Docs/Manual%20do%20Usuario.pdf)
> - **Project modules:**
>    - **Frontend:** [open frontend](frontend)
>    - **Backend:**  [open backend](backend)
> - **API documentation:**
>    - **Endpoint documentation:** Swagger at `http://localhost:8080/swagger-ui/index.html` (after running the backend)
>    - **User Guide:** [open User Guide](docs/Docs/Manual%20do%20Usuario.pdf)

→ [Back to top](#top)

<span id="technologies">

## 🛠️ Technologies

The following tools, languages, libraries and technologies were used to build the project:

![Figma](https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white)
![Java](https://img.shields.io/badge/Java-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![Nuxt.js](https://img.shields.io/badge/Nuxt.js-00DC82?style=for-the-badge&logo=nuxtdotjs&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
<br>
![VS_Code](https://img.shields.io/badge/VS_Code-CED4DA?style=for-the-badge&logo=visual-studio-code&logoColor=0078D4)
![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)
![Jira](https://img.shields.io/badge/Jira-0052CC?style=for-the-badge&logo=jira&logoColor=white)
![Google_Docs](https://img.shields.io/badge/Google%20Docs-CED4DA?style=for-the-badge&logo=google-docs&logoColor=0D96F6)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)


→ [Back to top](#top)

<span id="team">

## :busts_in_silhouette: Team

|    Role       | Name                  | LinkedIn & GitHub |
|---------------|-----------------------|-------------------|
| Product Owner | Beatriz Sthefanny | [![Linkedin](https://img.shields.io/badge/Linkedin-blue?logo=Linkedin&logoColor=white)](https://www.linkedin.com/in/beatriz-santos-0b6773220/) [![GitHub](https://img.shields.io/badge/GitHub-111217?logo=github&logoColor=white)](https://github.com/BeatrizSantos00) |
| Scrum Master  | Gleialison Rezende | [![Linkedin](https://img.shields.io/badge/Linkedin-blue?logo=Linkedin&logoColor=white)](https://www.linkedin.com/in/gleialison-rezende-835453b0/) [![GitHub](https://img.shields.io/badge/GitHub-111217?logo=github&logoColor=white)](https://github.com/Glei-Rezende) |
| Dev Team      | Caio Osorio         | [![Linkedin](https://img.shields.io/badge/Linkedin-blue?logo=Linkedin&logoColor=white)](https://www.linkedin.com/in/caiovosorio/) [![GitHub](https://img.shields.io/badge/GitHub-111217?logo=github&logoColor=white)](https://github.com/User-Standart) |
| Dev Team      | Rafael Slivka       | [![Linkedin](https://img.shields.io/badge/Linkedin-blue?logo=Linkedin&logoColor=white)](https://www.linkedin.com/in/rafael-lopes-slivka-07753326a/) [![GitHub](https://img.shields.io/badge/GitHub-111217?logo=github&logoColor=white)](https://github.com/rafaslivka) |
| Dev Team      | Tiago Bernardo      | [![Linkedin](https://img.shields.io/badge/Linkedin-blue?logo=Linkedin&logoColor=white)](https://www.linkedin.com/in/tiagobernardosantos/) [![GitHub](https://img.shields.io/badge/GitHub-111217?logo=github&logoColor=white)](https://github.com/TiagoBernardoSantos) |
| Dev Team      | Victor Ryan         | [![Linkedin](https://img.shields.io/badge/Linkedin-blue?logo=Linkedin&logoColor=white)](https://www.linkedin.com/in/victor-ryan-51738b261) [![GitHub](https://img.shields.io/badge/GitHub-111217?logo=github&logoColor=white)](https://github.com/yzvictorr) |

→ [Back to top](#top)
