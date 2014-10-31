{:dev
  {:env
    {
     :hivewing-aws-access-key "123abc",
     :hivewing-aws-secret-key "123abc",
     :hivewing-aws-endpoint   "http://localhost:3800",
     :hivewing-aws-dynamo-worker-table "HivewingWorkerConfiguration.v1"
    },
  :test
    {
     :hivewing-aws-access-key "123abc",
     :hivewing-aws-secret-key "123abc",
     :hivewing-aws-endpoint   "http://localhost:3800",
     :hivewing-aws-dynamo-worker-table "test.HivewingWorkerConfiguration.v1"
    }
  }
}
