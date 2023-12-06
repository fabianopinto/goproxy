package main

import (
	"golang-rest/app"
	"log"
	"os"
)

func main() {
	port := os.Getenv("SERVER_PORT")
	if port == "" {
		port = "8080"
	}
	if err := app.ListenAndServe(port); err != nil {
		log.Fatal(err)
	}
}
