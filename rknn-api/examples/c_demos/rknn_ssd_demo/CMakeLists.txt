cmake_minimum_required(VERSION 3.4.1)

project(rknn_ssd_demo)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS}")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

if(CMAKE_SIZEOF_VOID_P EQUAL 8)
	message(STATUS "64bit")
	set(LIB_ARCH lib64)
else()
	message(STATUS "32bit")
	set(LIB_ARCH lib)
endif()

# rknn api
set(RKNN_API_PATH ${CMAKE_SOURCE_DIR}/../../../librknn_api)
include_directories(${RKNN_API_PATH}/include)
set(RKNN_API_LIB ${RKNN_API_PATH}/${CMAKE_SYSTEM_NAME}/${LIB_ARCH}/librknn_api.so)

#stb
include_directories(${CMAKE_SOURCE_DIR}/../3rdparty/)

set(CMAKE_INSTALL_RPATH "lib")

add_executable(rknn_ssd_demo
        src/main.cc
        src/ssd.cc
        )

target_link_libraries(rknn_ssd_demo
	${RKNN_API_LIB}
	dl
)

if (CMAKE_SYSTEM_NAME STREQUAL "Linux")
	target_link_libraries(rknn_ssd_demo
		${RKNN_API_LIB}
		pthread
		dl
	)
else()
	target_link_libraries(rknn_ssd_demo
		${RKNN_API_LIB}
		dl
	)
endif()

# install target and libraries
set(CMAKE_INSTALL_PREFIX ${CMAKE_SOURCE_DIR}/install/rknn_ssd_demo_${CMAKE_SYSTEM_NAME})
install(TARGETS rknn_ssd_demo DESTINATION ./)
install(DIRECTORY model DESTINATION ./)
install(PROGRAMS run_demo.sh DESTINATION ./)
install(PROGRAMS ${RKNN_API_LIB} DESTINATION lib)
