package poc

import (
	"bytes"
	"github.com/cssivision/reverseproxy"
	"io"
	"net/http"
	"net/url"
)

type CustomProxy struct {
	reverseproxy.ReverseProxy
	url *url.URL
}

func (p *CustomProxy) ServeProxy(w http.ResponseWriter, r *http.Request, body []byte) {
	r.URL.Scheme = p.url.Scheme
	r.URL.Host = p.url.Host
	r.Body = io.NopCloser(bytes.NewReader(body))
	p.ServeHTTP(w, r)
}

func NewProxy(target string) CustomProxy {
	var proxy CustomProxy
	proxy.Director = func(_ *http.Request) {
		// nop
	}
	proxy.url, _ = url.Parse(target)
	return proxy
}
