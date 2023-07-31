FROM python:3.8-slim-bullseye

RUN apt-get update  \
    && apt-get dist-upgrade -y \
    && apt-get install make -y \
    && apt-get install wget -y
