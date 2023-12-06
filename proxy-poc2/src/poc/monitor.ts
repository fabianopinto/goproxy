export function monitor() {
  setInterval(() => {
    const mem = process.memoryUsage();
    const cpu = process.cpuUsage();
    console.log(`goproxy2 monitor -- cpuUser: ${cpu.user}, cpuSystem: ${cpu.user}, heapUsed: ${mem.heapUsed}, heapTotal: ${mem.heapTotal}`);
  }, 10_000);
}
