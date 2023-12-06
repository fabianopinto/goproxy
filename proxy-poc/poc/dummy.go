package poc

import (
	"net/http"
)

type dummyResponseWriter struct{}

func (w dummyResponseWriter) Header() http.Header {
	return http.Header{}
}

func (w dummyResponseWriter) Write(bytes []byte) (int, error) {
	return len(bytes), nil
}

func (w dummyResponseWriter) WriteHeader(_ int) {
	// nop
}

var _ http.ResponseWriter = (*dummyResponseWriter)(nil)
