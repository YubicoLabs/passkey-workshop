.PHONY: all client validate refresh clean

all: client

client: common.sh token.json
	./client.sh

token.json: common.sh
	./token.sh

refresh: common.sh token.json
	./refresh.sh

validate: common.sh token.json
	./validate.sh

clean:
	-rm token.json jwks.json
