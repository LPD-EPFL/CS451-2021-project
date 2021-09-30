#ifndef SENDER_H
#define SENDER_H

#include <iostream>
#include <arpa/inet.h>
#include <netdb.h>
#include <sys/socket.h>
#include <sys/types.h>

class Sender {

    public:

    Sender(in_addr_t ip, unsigned short port);

    ~Sender();

    ssize_t send(char msg, in_addr_t to_ip, unsigned short to_port);


    private:

    int socketDescriptor;
    in_addr_t sender_ip;
    unsigned short sender_port;

};

#endif