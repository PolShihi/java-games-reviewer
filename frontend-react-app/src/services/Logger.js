import log from 'loglevel';

if (import.meta.env.DEV) {
  log.setLevel(log.levels.DEBUG);
} else {
  log.setLevel(log.levels.WARN);
}

const originalFactory = log.methodFactory;
log.methodFactory = function (methodName, logLevel, loggerName) {
  const rawMethod = originalFactory(methodName, logLevel, loggerName);
  
  return function (...messages) {
    const timestamp = new Date().toLocaleTimeString();
    const style = 'color: gray; font-weight: lighter;'; 
    if (messages.length === 0) {
      rawMethod(`%c[${timestamp}]`, style);
      return;
    }

    rawMethod(`%c[${timestamp}]`, style, ...messages);
  };
};

log.setLevel(log.getLevel()); 

export default log;
