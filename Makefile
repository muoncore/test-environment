
run: build docker-run

NPMCORGIS := env-node env-jvm
.PHONY: all test $(NPMCORGIS)

$(NPMCORGIS):
	$(MAKE) -C $@

stop: docker-stop
clean: docker-stop dockerclean
	docker-compose down

build: $(NPMCORGIS) docker

docker:
	docker-compose build

docker-run:
	docker-compose up -d

docker-stop:
	docker-compose stop

redeploy: dockerclean build docker-run

dockerclean:
	docker-compose down
	docker-compose rm -f --all
