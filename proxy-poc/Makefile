#!/usr/bin/make -f

OUTPUT_DIR := ./build
BINARY_NAME := goproxy
GO := go
PLATFORMS := $(shell $(GO) tool dist list)

.PHONY: all build $(PLATFORMS) clean run deps test cover vet

all: build

build: clean $(PLATFORMS)

$(PLATFORMS):
	@mkdir -p $(OUTPUT_DIR)
	-GOOS=$(@D) GOARCH=$(@F) $(GO) build -o $(OUTPUT_DIR)/$(BINARY_NAME)-$(@D)-$(@F)

clean:
	$(GO) clean
	$(RM) -r $(OUTPUT_DIR)
	$(RM) coverage.out

run:
	$(GO) run .

deps:
	$(GO) mod download

test:
	$(GO) test ./...

cover:
	$(GO) test ./... -coverprofile cover.out

vet:
	$(GO) vet ./...
