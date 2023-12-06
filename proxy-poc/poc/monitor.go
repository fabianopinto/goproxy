package poc

import (
	"log"
	"runtime"
	"time"
)

func Monitor() {
	go func() {
		for {
			cpu := runtime.NumCPU()
			rot := runtime.NumGoroutine()
			mem := &runtime.MemStats{}
			runtime.ReadMemStats(mem)

			log.Printf("goproxy monitor -- CPU: %d, Goroutine: %d, Memory: %d", cpu, rot, mem.Alloc)
			time.Sleep(10 * time.Second)
		}
	}()
}
