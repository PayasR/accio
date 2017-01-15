/*
 * Accio is a program whose purpose is to study location privacy.
 * Copyright (C) 2016 Vincent Primault <vincent.primault@liris.cnrs.fr>
 *
 * Accio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Accio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Accio.  If not, see <http://www.gnu.org/licenses/>.
 */

namespace java fr.cnrs.liris.accio.core.domain

include "fr/cnrs/liris/accio/core/domain/common.thrift"

/**
 * Declaration of resources an operator required to execute.
 */
struct Resource {
  // Fractional number of CPUs.
  1: required double cpu;

  // Quantity of free memory, in MB.
  2: required i64 ram_mb;

  // Quantity of free disk space, in MB.
  3: required i64 disk_mb;
}

/**
 * Definition of an operator port (either input or output).
 */
struct ArgDef {
  // Input name. Should be unique among all inputs of a given operator.
  1: required string name;

  // One-line help text.
  2: optional string help;

  // Data type.
  3: required common.DataType kind;

  // Whether this parameter is optional and does not have to be specified. It should be false for output ports.
  4: required bool is_optional;

  // Default value taken by this input if none is specified. It should be empty for output ports.
  5: optional common.Value default_value;
}

/**
 * Definition of an operator.
 */
struct OpDef {
  // Operator name. Should be unique among all operators.
  1: required string name;

  // Category. Only used for presentational purposes.
  2: required string category;

  // One-line help text.
  3: optional string help;

  // Longer description of what the operator does.
  4: optional string description;

  // Definition of inputs the operator consumes.
  5: required list<ArgDef> inputs;

  // Definition of outputs the operator produces.
  6: required list<ArgDef> outputs;

  // Deprecation message, if this operator is actually deprecated.
  7: optional string deprecation;

  // Declaration of resources this operator needs to be executed.
  8: required Resource resource;
}

/**
 * Payload containing everything needed to execute an operator. This structure embeds only the strict minimum of
 * information to ensure reproducibility of executions. For example, it means it cannot include information such
 * as the node name (the execution of an operator should not depend on the actual node name, only on the operator
 * name). This structure is used to compute a cache key for the result (among other things).
 */
struct OpPayload {
  // Name of the operator to execute.
  1: required string op;

  // Seed used by unstable operators (included even if the operator is not unstable);
  2: required i64 seed;

  // Mapping between a port name and its value. It should contain at least required inputs.
  3: required map<string, common.Value> inputs;
}

/**
 * Result of the execution of an operator. Once again, it includes only information that is reproducible. For
 * example, it means it cannot include information such as started/completed times (but it can include duration).
 * This structure is what can be cached and re-used for another identical operator execution.
 */
struct OpResult {
  // Exit code. It is particularly useful for operators launching external executables and monitoring their execution.
  // For built-in operators, it should still be included. 0 means a successful execution, any other value represents
  // a failed execution.
  1: required i32 exit_code;

  // Error captured during the operator execution. This should only be set of the exit code indicates a failure, but
  // it does not have to (e.g., if an operator cannot get structure exception information).
  2: optional common.Error error;

  // Arfifacts produced by the operator execution. This should be left empty if the exit code indicates a failure.
  // If filled, there should be an artifact per output port of the operator, no more and no less.
  3: required set<common.Artifact> artifacts;

  // Metrics produced by the operator execution. This can always be filled, whether or not the execution is successful.
  // There is no definition of metrics produced by an operator, so any relevant metrics can be included here and will
  // be exposed thereafter.
  4: required set<common.Metric> metrics;

  // Cache key generated by this operator, used for result memoization.
  5: required string cache_key;
}