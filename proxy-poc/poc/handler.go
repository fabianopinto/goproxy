package poc

import (
	"context"
	"fmt"
	"net/http"
)

func CustomHandler(from, to string) http.HandlerFunc {
	source := NewProxy(from)
	target := NewProxy(to)

	return func(w http.ResponseWriter, r *http.Request) {
		body, group, status, _ := parseRequest(r)
		leader, shadow := source, target
		if status == "COMPLETE" {
			leader, shadow = target, source
		}

		leader.ModifyResponse = func(res *http.Response) error {
			res.Header.Set("X-goproxy", fmt.Sprintf("group=%s;status=%s", group, status))
			return nil
		}
		shadow.ModifyResponse = nil

		leader.ServeProxy(w, r, body)
		shadow.ServeProxy(dummyResponseWriter{}, r.Clone(context.TODO()), body)
	}
}
