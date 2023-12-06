package poc

import (
	"context"
	"github.com/redis/go-redis/v9"
	"io"
	"log"
	"net/http"
	"regexp"
)

var (
	regexp1, _ = regexp.Compile("^/contact/(\\w+).*$")
	regexp2, _ = regexp.Compile("(?s)^.*\"group\":\\s*\"(\\w+)\".*$")

	ctx = context.Background()
	rdb = redis.NewClient(&redis.Options{Addr: "localhost:6379", Password: "", DB: 0})
)

func parseRequest(r *http.Request) (body []byte, group string, status string, err error) {
	if body, err = io.ReadAll(r.Body); err != nil {
		log.Printf("failed to read request body -- %s", err.Error())
	}

	path := r.URL.Path
	if regexp1.MatchString(path) {
		group = regexp1.ReplaceAllString(path, "$1")
	} else if regexp2.Match(body) {
		group = string(regexp2.ReplaceAll(body, []byte("$1")))
	} else {
		log.Print("failed to parse \"group\" property")
	}

	if group != "" {
		if status, err = rdb.Get(ctx, group).Result(); err != nil {
			log.Printf("failed to fetch \"%s\" status -- %s", group, err.Error())
		}
	}
	return
}
