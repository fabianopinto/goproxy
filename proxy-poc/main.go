package main

import (
	"goproxy/poc"
	"log"
	"net/http"
)

func main() {
	poc.Monitor()

	log.Fatal(http.ListenAndServe(":8080",
		poc.CustomHandler("http://localhost:8081", "http://localhost:8082")))
}
