import log from 'loglevel';

if (import.meta.env.DEV) {
  log.setLevel(log.levels.DEBUG);
} else {
  log.setLevel(log.levels.WARN);
}

const originalFactory = log.methodFactory;
log.methodFactory = function (methodName, logLevel, loggerName) {
  const rawMethod = originalFactory(methodName, logLevel, loggerName);
  
  return function (message) {
    const timestamp = new Date().toLocaleTimeString();
    const style = 'color: gray; font-weight: lighter;'; 
    rawMethod(`%c[${timestamp}] ${message}`, style);
  };
};

log.setLevel(log.getLevel()); 

export default log;