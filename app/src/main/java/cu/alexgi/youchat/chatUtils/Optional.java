/*
 * Copyright (C) 2011 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cu.alexgi.youchat.chatUtils;

import java.io.Serializable;
import java.util.Set;

import static cu.alexgi.youchat.chatUtils.Preconditions.checkNotNull;

public abstract class Optional<T> implements Serializable {
  /**
   * Returns an {@code Optional} instance with no contained reference.
   */
  @SuppressWarnings("unchecked")
  public static <T> Optional<T> absent() {
    return (Optional<T>) Absent.INSTANCE;
  }

  /**
   * Returns an {@code Optional} instance containing the given non-null reference.
   */
  public static <T> Optional<T> of(T reference) {
    return new Present<T>(checkNotNull(reference));
  }

  /**
   * If {@code nullableReference} is non-null, returns an {@code Optional} instance containing that
   * reference; otherwise returns {@link Optional#absent}.
   */
  public static <T> Optional<T> fromNullable(T nullableReference) {
    return (nullableReference == null)
        ? Optional.<T>absent()
        : new Present<T>(nullableReference);
  }

  Optional() {}

  /**
   * Returns {@code true} if this holder contains a (non-null) instance.
   */
  public abstract boolean isPresent();


  public abstract T get();

  /**
   * Returns the contained instance if it is present; {@code defaultValue} otherwise. If
   * no default value should be required because the instance is known to be present, use
   * {@link #get()} instead. For a default value of {@code null}, use {@link #orNull}.
   *
   * <p>Note about generics: The signature {@code public T or(T defaultValue)} is overly
   * restrictive. However, the ideal signature, {@code public <S super T> S or(S)}, is not legal
   * Java. As a result, some sensible operations involving subtypes are compile errors:
   * <pre>   {@code
   *
   *   Optional<Integer> optionalInt = getSomeOptionalInt();
   *   Number value = optionalInt.or(0.5); // error
   *
   *   FluentIterable<? extends Number> numbers = getSomeNumbers();
   *   Optional<? extends Number> first = numbers.first();
   *   Number value = first.or(0.5); // error}</pre>
   *
   * As a workaround, it is always safe to cast an {@code Optional<? extends T>} to {@code
   * Optional<T>}. Casting either of the above example {@code Optional} instances to {@code
   * Optional<Number>} (where {@code Number} is the desired output type) solves the problem:
   * <pre>   {@code
   *
   *   Optional<Number> optionalInt = (Optional) getSomeOptionalInt();
   *   Number value = optionalInt.or(0.5); // fine
   *
   *   FluentIterable<? extends Number> numbers = getSomeNumbers();
   *   Optional<Number> first = (Optional) numbers.first();
   *   Number value = first.or(0.5); // fine}</pre>
   */
  public abstract T or(T defaultValue);

  /**
   * Returns this {@code Optional} if it has a value present; {@code secondChoice}
   * otherwise.
   */
  public abstract Optional<T> or(Optional<? extends T> secondChoice);

  public abstract T or(Supplier<? extends T> supplier);

  /**
   * Returns the contained instance if it is present; {@code null} otherwise. If the
   * instance is known to be present, use {@link #get()} instead.
   */
  public abstract T orNull();

  /**
   * Returns an immutable singleton {@link Set} whose only element is the contained instance
   * if it is present; an empty immutable {@link Set} otherwise.
   *
   * @since 11.0
   */
  public abstract Set<T> asSet();


  public abstract <V> Optional<V> transform(Function<? super T, V> function);


  @Override public abstract boolean equals(Object object);

  /**
   * Returns a hash code for this instance.
   */
  @Override public abstract int hashCode();

  /**
   * Returns a string representation for this instance. The form of this string
   * representation is unspecified.
   */
  @Override public abstract String toString();

  /**
   * Returns the value of each present instance from the supplied {@code optionals}, in order,
   * skipping over occurrences of {@link Optional#absent}. Iterators are unmodifiable and are
   * evaluated lazily.
   *
   * @since 11.0 (generics widened in 13.0)
   */

//  public static <T> Iterable<T> presentInstances(
//      final Iterable<? extends Optional<? extends T>> optionals) {
//    checkNotNull(optionals);
//    return new Iterable<T>() {
//      @Override public Iterator<T> iterator() {
//        return new AbstractIterator<T>() {
//          private final Iterator<? extends Optional<? extends T>> iterator =
//              checkNotNull(optionals.iterator());
//
//          @Override protected T computeNext() {
//            while (iterator.hasNext()) {
//              Optional<? extends T> optional = iterator.next();
//              if (optional.isPresent()) {
//                return optional.get();
//              }
//            }
//            return endOfData();
//          }
//        };
//      };
//    };
//  }

  private static final long serialVersionUID = 0;
}
