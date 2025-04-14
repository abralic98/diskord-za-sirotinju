
package com.example.demo.controller.global;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

  @Override
  protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
    if (ex instanceof ModifiedException) {
      return GraphqlErrorBuilder.newError(env)
          .message(ex.getMessage())
          .errorType(ErrorType.BAD_REQUEST)
          .build();
    }

    return null; // Let Spring handle other exceptions as INTERNAL_ERROR
  }
}
