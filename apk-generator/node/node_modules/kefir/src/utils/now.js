export default Date.now ?
  (() => Date.now()) :
  (() => new Date().getTime());
