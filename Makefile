.PHONY: help build start stop test test-docker clean logs

help: ## Show this help message
	@echo 'Usage: make [target]'
	@echo ''
	@echo 'Targets:'
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  %-15s %s\n", $$1, $$2}' $(MAKEFILE_LIST)

build: ## Build all modules
	mvn clean package -DskipTests

start: ## Start the Triangle service
	./run-service.sh

stop: ## Stop all services
	docker compose down

test: ## Run API tests locally
	./test-service.sh local

test-docker: ## Run API tests in Docker
	./test-service.sh docker

clean: ## Clean build artifacts
	mvn clean
	docker compose down --rmi local --volumes

logs: ## Show service logs
	docker compose logs -f triangle-service

status: ## Show running containers
	docker compose ps