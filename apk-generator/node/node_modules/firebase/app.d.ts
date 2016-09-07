/*! @license Firebase v3.3.0
    Build: 3.3.0-rc.7
    Terms: https://developers.google.com/terms */
declare namespace firebase {
  interface FirebaseError {
    code: string;
    message: string;
    name: string;
    stack: string;
  }

  class Promise<T> extends Promise_Instance<T> {
    static all(values: firebase.Promise<any>[]): firebase.Promise<any[]>;
    static reject(error: Error): firebase.Promise<any>;
    static resolve<T>(value?: T): firebase.Promise<T>;
  }
  class Promise_Instance<T> implements firebase.Thenable<any> {
    constructor(
        resolver:
            (a?: (a: T) => undefined, b?: (a: Error) => undefined) => any);
    catch (onReject?: (a: Error) => any): firebase.Thenable<any>;
    then(onResolve?: (a: T) => any, onReject?: (a: Error) => any):
        firebase.Promise<any>;
  }

  var SDK_VERSION: string;

  interface Thenable<T> {
    catch (onReject?: (a: Error) => any): any;
    then(onResolve?: (a: T) => any, onReject?: (a: Error) => any):
        firebase.Thenable<any>;
  }

  function app(name: string): firebase.app.App;

  var apps: (firebase.app.App|null)[];

  function initializeApp(options: Object, name?: string): firebase.app.App;
}

declare namespace firebase.app {
  interface App {
    delete (): firebase.Promise<any>;
    name: string;
    options: Object;
  }
}

declare module 'firebase' {
  export = firebase;
}
