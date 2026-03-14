# BUAA Object-Oriented Programming 2025

Coursework repository for the **Object-Oriented Programming (OO)** course at Beihang University.

This repository records the complete development process of the course projects, including the preparation project and four major units.  
Each unit is implemented through **three iterative assignments**, demonstrating incremental system design and refinement.

---

# Table of Contents

- [BUAA Object-Oriented Programming 2025](#buaa-object-oriented-programming-2025)
- [Table of Contents](#table-of-contents)
- [Repository Structure](#repository-structure)
- [Projects Overview](#projects-overview)
  - [Pre-OOP: Adventure Game](#pre-oop-adventure-game)
  - [Unit1: Expression Evaluator](#unit1-expression-evaluator)
  - [Unit2: Elevator System](#unit2-elevator-system)
  - [Unit3: Social Network with JML](#unit3-social-network-with-jml)
  - [Unit4: UML Library System](#unit4-uml-library-system)
- [Design Evolution](#design-evolution)
- [Technologies](#technologies)

---

# Repository Structure
BUAA-OO-2025
│
├── Pre-Adventures-Game
│ ├── src # Source code for the adventure game
│ ├── test # JUnit test cases
│ ├── images # Diagrams and documentation images
│ └── Summary.md # Design summary and reflection
│
├── Unit1-Expression
│ ├── Iter1 # Iteration 1 implementation
│ ├── Iter2 # Iteration 2 implementation
│ ├── Iter3 # Iteration 3 implementation
│ ├── images # Expression parser design diagrams
│ └── Summary.md # Unit summary
│
├── Unit2-Elevator
│ ├── Iter1
│ ├── Iter2
│ ├── Iter3
│ ├── images # Architecture and scheduling diagrams
│ └── Summary.md
│
├── Unit3-JML-Social-Network
│ ├── Iter1
│ ├── Iter2
│ ├── Iter3
│ ├── images # Network model diagrams
│ └── Summary.md
│
├── Unit4-UML-Library-System
│ ├── Iter1
│ ├── Iter2
│ ├── Iter3
│ ├── images # UML diagrams
│ └── Summary.md
│
└── README.md # Overview of the entire repository


Each unit contains three iterations (`Iter1–Iter3`) showing the progressive improvement of the system.

---

# Projects Overview

## Pre-OOP: Adventure Game

An introductory project that implements a simple **adventure game system**.

Main features:

- Adventurer and item management
- Backpack system
- Bottle and equipment hierarchy
- Fragment and dungeon mechanics
- Employee recruitment

Concepts introduced:

- Java object-oriented programming
- Class design and inheritance
- Container structures
- Unit testing with JUnit

---

## Unit1: Expression Evaluator

This project implements a **symbolic expression evaluator** using **recursive descent parsing**.

Main features:

- polynomial expressions
- trigonometric functions
- nested expressions
- symbolic differentiation

Core components:

- lexer
- parser
- expression tree
- polynomial simplification

---

## Unit2: Elevator System

A **multi-threaded elevator scheduling system** that simulates passenger transportation in a building.

Main components:

- InputThread
- RequestQueue
- RequestScheduler
- Elevator threads

Key concepts:

- producer–consumer pattern
- thread synchronization
- concurrent request scheduling
- dynamic elevator assignment

---

## Unit3: Social Network with JML

Implementation of a **social network system** based on **JML (Java Modeling Language)** specifications.

Core model:

- graph-based social network
- persons and relations
- message transmission system

Key tasks:

- implement methods according to formal specifications
- design comprehensive test cases
- optimize algorithms for performance

---

## Unit4: UML Library System

A **library management system** designed through **UML modeling**.

Design process:

1. requirement analysis  
2. conceptual modeling  
3. UML class diagram design  
4. system implementation  

Main modules:

- library management
- book shelves
- borrowing system
- appointment system
- reading room management

---

# Design Evolution

The course gradually introduces more advanced software engineering concepts.

| Stage | Focus |
|-----|-----|
| Pre-OOP | Basic object-oriented programming |
| Unit1 | Parsing algorithms and modular design |
| Unit2 | Concurrent system design |
| Unit3 | Formal specification and testing |
| Unit4 | UML-based software modeling |

---

# Technologies

- Java
- Object-Oriented Design
- Multithreading
- JML
- UML
- JUnit
- Git