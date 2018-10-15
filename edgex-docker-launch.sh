#!/bin/sh

# Copyright (c) 2018
# Cavium
#
# SPDX-License-Identifier: Apache-2.0
#

# Start EdgeX Foundry services in right order, as described:
# https://wiki.edgexfoundry.org/display/FA/Get+EdgeX+Foundry+-+Users

COMPOSE_FILE=./docker-compose.yml

echo "Starting volume"
docker-compose -f $COMPOSE_FILE up -d volume
echo "Starting mongo"
docker-compose -f $COMPOSE_FILE up -d mongo
echo "Starting consul"
docker-compose -f $COMPOSE_FILE up -d config-seed
# echo "Starting consul"
# docker-compose -f $COMPOSE_FILE up -d consul

echo "Sleeping before launching remaining services"
sleep 15

echo "Starting notifications"
docker-compose -f $COMPOSE_FILE up -d notifications
echo "Starting support-logging"
docker-compose -f $COMPOSE_FILE up -d logging
echo "Starting core-metadata"
docker-compose -f $COMPOSE_FILE up -d metadata
echo "Starting core-data"
docker-compose -f $COMPOSE_FILE up -d data
echo "Starting core-command"
docker-compose -f $COMPOSE_FILE up -d command
echo "Starting core-export-client"
docker-compose -f $COMPOSE_FILE up -d export-client
echo "Starting core-export-distro"
docker-compose -f $COMPOSE_FILE up -d export-distro


##################################################################
#                      DEVICE SERVICES                           #
##################################################################

# echo "Starting device-virtual"
# docker-compose -f $COMPOSE_FILE up -d device-virtual

# echo "Starting my-device-virtual"
# docker-compose -f $COMPOSE_FILE up -d my-device-virtual

# echo "Starting device-cnc"
# docker-compose -f $COMPOSE_FILE up -d device-cnc

# echo "Starting device-opcua"
# docker-compose -f $COMPOSE_FILE up -d device-opcua-java