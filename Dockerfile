FROM ubuntu:latest
LABEL authors="chanyoungpark"

ENTRYPOINT ["top", "-b"]