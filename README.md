\# PROJECT KEYSTONE



\## Field Service Management Platform



PROJECT KEYSTONE is a full-stack Field Service Management Platform developed to manage customers, service sites, work orders, technicians, parts, time logs, and service status tracking from a single application.



The project follows a role-based architecture where different users can access features according to their responsibilities.



\---



\## Features



\### Authentication \& Security

\- User registration and login

\- JWT-based authentication

\- BCrypt password hashing

\- Role-based authorization

\- Protected REST APIs

\- Customer, Manager, Dispatcher and Technician access control



\### Customer Management

\- Create and manage customers

\- Search customers

\- Customer-specific profile access

\- Customer site management



\### Site Management

\- Create and manage customer service sites

\- Link sites with customers

\- Customer-specific site access



\### Work Order Management

\- Create work orders

\- Automatic work order code generation

\- Assign technicians

\- Set work order priority

\- Update work order status

\- Edit work orders

\- Delete eligible work orders

\- Work order status history

\- Search and pagination

\- Customer work order portal

\- Technician work order access



\### Parts Management

\- Add and manage parts

\- Track available stock

\- Use parts in work orders

\- Automatically reduce stock when parts are used

\- Prevent invalid part usage



\### Time Log Management

\- Start and record technician work time

\- End time tracking

\- Add work notes

\- Technician-specific time logs

\- Work-order-based time tracking



\### Dashboard

\- Total work orders

\- Open work orders

\- Assigned work orders

\- In-progress work orders

\- Completed work orders

\- Closed work orders

\- Cancelled work orders

\- Total customers

\- Total sites

\- Total technicians



\---



\## Technology Stack



\### Backend

\- Java 21

\- Spring Boot 3.5.16

\- Spring Security

\- JWT

\- BCrypt

\- Spring Data JPA

\- Hibernate

\- PostgreSQL

\- Flyway



\### Frontend

\- React

\- TypeScript

\- Vite

\- HTML

\- CSS



\### Development Tools

\- Eclipse

\- Visual Studio Code

\- PostgreSQL

\- Git

\- GitHub

\- Maven

\- npm



\---



\## Project Structure



```text

project-keystone/

│

├── frontend/

│   ├── public/

│   └── src/

│       ├── App.tsx

│       ├── App.css

│       ├── index.css

│       └── main.tsx

│

├── keystone/

│   ├── src/

│   │   ├── main/

│   │   │   ├── java/com/keystone/

│   │   │   │   ├── auth/

│   │   │   │   ├── controller/

│   │   │   │   ├── dto/

│   │   │   │   ├── entity/

│   │   │   │   ├── exception/

│   │   │   │   ├── repository/

│   │   │   │   ├── security/

│   │   │   │   └── service/

│   │   │   │

│   │   │   └── resources/

│   │   │       ├── db/migration/

│   │   │       └── application.properties

│   │   │

│   │   └── test/

│   │

│   ├── pom.xml

│   └── mvnw

│

└── README.md

