#ifndef RECEIVER_H
#define RECEIVER_H

#include <iostream>
#include <arpa/inet.h>
#include <netdb.h>
#include <sys/socket.h>
#include <sys/types.h>

class Receiver {

    public:

    Receiver(in_addr_t ip, unsigned short port);

    ~Receiver();

    ssize_t receive();


    private:

    int socketDescriptor;
    in_addr_t receiver_ip;
    unsigned short receiver_port;

    struct sockaddr_in receiver_addr;

};

#endif