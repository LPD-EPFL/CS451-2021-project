# DO NAME THE SYMBOLIC VARIABLE `SOURCES`

include_directories(include)
set(SOURCES src/main.cpp src/hello.c src/sender.cpp src/receiver.cpp)

# DO NOT EDIT THE FOLLOWING LINE
find_package(Threads)
add_executable(da_proc ${SOURCES})
target_link_libraries(da_proc ${CMAKE_THREAD_LIBS_INIT})
